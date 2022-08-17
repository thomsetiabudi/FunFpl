// SPDX-License-Identifier: MIT
pragma solidity >=0.7.0 <0.9.0;

import "@openzeppelin/contracts/token/ERC1155/ERC1155.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

/**
 * https://github.com/maticnetwork/pos-portal/blob/master/contracts/common/ContextMixin.sol
 */
abstract contract ContextMixin {  
            
    function msgSender()
        internal
        view
        returns (address payable sender)
    {
        if (msg.sender == address(this)) {
            bytes memory array = msg.data;
            uint256 index = msg.data.length;
            assembly {
                // Load the 32 bytes word from memory with the address on the lower 20 bytes, and mask those.
                sender := and(
                    mload(add(array, index)),
                    0xffffffffffffffffffffffffffffffffffffffff
                )
            }
        } else {
            sender = payable(msg.sender);
        }
        return sender;
    }
}

contract DFPL is ERC1155, Ownable, ContextMixin {
    string private _contractUri = "https://bafybeifp4bf2u4ykf5uuky3cuf67zrdkcwtybuw74f3wsbcdi645ey45au.ipfs.nftstorage.link/DFPL.json";
    string private _baseTokenUri = "https://bafybeifp4bf2u4ykf5uuky3cuf67zrdkcwtybuw74f3wsbcdi645ey45au.ipfs.nftstorage.link/DFPL";

    event PermanentURI(string _value, uint256 indexed _id);

    constructor() ERC1155("https://bafybeifp4bf2u4ykf5uuky3cuf67zrdkcwtybuw74f3wsbcdi645ey45au.ipfs.nftstorage.link/DFPL{id}.json") {
        _mint(msg.sender, 0, 10**18, "");
        for(uint256 gw = 1; gw <= 38; gw++) {
            _mint(msg.sender, gw, 1, "");
        }
        _mint(msg.sender, 100, 1, "");
    }

    function contractURI() public view returns (string memory) {
        return _contractUri;
    }

    function setContractURI(string memory _newContractUri) public onlyOwner {
        _contractUri = _newContractUri;
    }

    function setBaseTokenURI(string memory _newBaseTokenUri) public onlyOwner {
        _baseTokenUri = _newBaseTokenUri;
    }

    function freezeMetadata(uint256 _frozenTokenId) public onlyOwner {
        emit PermanentURI(uri(_frozenTokenId), _frozenTokenId);
    }

    function uri(uint256 _tokenId) override public view returns (string memory) {
        return string (
            abi.encodePacked(
                _baseTokenUri,
                Strings.toString(_tokenId),
                ".json"
            )
        );
    }

    /**
    * Override isApprovedForAll to auto-approve OpenSea's proxy contract
    */
    function isApprovedForAll(
        address _owner,
        address _operator
    ) public override view returns (bool isOperator) {
        // if OpenSea's ERC1155 Proxy Address is detected, auto-return true
        // for Polygon's Mumbai testnet, use 0x53d791f18155C211FF8b58671d0f7E9b50E596ad
       if (_operator == address(0x207Fa8Df3a17D96Ca7EA4f2893fcdCb78a304101)) {
            return true;
        }
        // otherwise, use the default ERC1155.isApprovedForAll()
        return ERC1155.isApprovedForAll(_owner, _operator);
    }

    /**
     * This is used instead of msg.sender as transactions won't be sent by the original token owner, but by OpenSea.
     */
    function _msgSender()
        internal
        override
        view
        returns (address sender)
    {
        return ContextMixin.msgSender();
    }
}
