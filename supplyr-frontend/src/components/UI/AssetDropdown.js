import React, {useEffect, useState} from 'react';
import '../Overview/Overview.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Cookie from 'js-cookie';
import config from '../../config.json';

import {Dropdown, DropdownButton,} from 'react-bootstrap';

const fetchAssetData = async () => {
    let token = Cookie.get('token');
    const assetUrl = config.API_URL + '/assets';
    return fetch(assetUrl, {
        method: 'GET',
        headers: {
            Authorization: 'Bearer ' + token,
            accept: 'application/json',
            'Content-Type': 'application/json',
        },
    })
        .then((res) => res.json())
        .then((data) => {
            return data;
        })
        .catch((error) => {
            console.log(error.message);
        });
};

export const AssetDropdown = (props) => {
    const [assetData, setAssetData] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(async () => {
        await fetchAssetData().then((data) => {
            setAssetData(data);
            setLoading(false);
        });
    }, []);

    if (!loading) {
        return (
            <DropdownButton
                variant="secondary"
                className="dropdown"
                id="dropdown-basic-button"
                title={props.selectedAsset}
            >
                {assetData.map((asset, index) => {
                    return (
                        <Dropdown.Item
                            key={index}
                            onClick={() => props.setAsset(asset.name)}
                        >
                            {asset.name}
                        </Dropdown.Item>
                    );
                })}
            </DropdownButton>
        );
    } else {
        return (
            <DropdownButton
                variant="secondary"
                className="dropdown"
                id="dropdown-basic-button"
                title="loading..."
            ></DropdownButton>
        );
    }
};
