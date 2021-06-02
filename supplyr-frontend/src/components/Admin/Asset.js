import React, {useState} from 'react';
import {Button, Container, Form,} from 'react-bootstrap';
import './Admin.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Cookies from 'js-cookie';
import {AssetDropdown} from '../UI/AssetDropdown';
import {UnitDropdown} from '../UI/UnitDropdown';
import 'react-toastify/dist/ReactToastify.css';
import {toast} from 'react-toastify';
import config from '../../config.json';

toast.configure();

const success = (setResponse, message) => {
    toast.success(message, {
        position: toast.POSITION.BOTTOM_RIGHT,
    });
    setResponse(null);
};

const error = (error, setResponse) => {
    toast.error(error, {
        position: toast.POSITION.BOTTOM_RIGHT,
    });
    setResponse(null);
};
const createAsset = async (assetName, setResponse) => {
    const url = config.API_URL + '/assets';
    const token = Cookies.get('token');

    return fetch(url, {
        method: 'POST',
        headers: {
            Authorization: 'Bearer ' + token,
            accept: 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({name: assetName}),
    })
        .then((res) => res.json())
        .then((data) => {
            setResponse(data);
        })
        .catch((error) => {
            setResponse(error);
        });
};

const addAssetToUnit = async (
    addAssetName,
    addAssetUnit,
    assetQuantity,
    setAddAssetResponse
) => {
    const url = config.API_URL + '/assets';
    const token = Cookies.get('token');

    return fetch(url, {
        method: 'PUT',
        headers: {
            Authorization: 'Bearer ' + token,
            accept: 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            organisationalUnitName: addAssetUnit,
            assetName: addAssetName,
            quantity: assetQuantity,
        }),
    })
        .then((res) => res.json())
        .then((data) => {
            setAddAssetResponse(data);
        })
        .catch((error) => {
            setAddAssetResponse(error);
        });
};

export const Asset = () => {
    const [assetName, setAssetName] = useState(null);
    const [addAssetName, setAddAssetName] = useState('Select Asset');
    const [addAssetUnit, setAddAssetUnit] = useState('Select Unit');
    const [assetQuantity, setAssetQuantity] = useState(null);
    const [response, setResponse] = useState(null);
    const [addAssetResponse, setAddAssetResponse] = useState(null);
    const [id, setId] = useState('123');

    const forceUpdate = React.useCallback(() => {
        setAddAssetName('Select Asset');
        setAssetName(null);
        setAddAssetUnit('Select Unit');
        setAssetQuantity(null);
        setResponse(null);
        setAddAssetResponse(null);
    }, []);

    return (
        <Container>
            <div className="admin-card">
                <h2 className="admin-card-title">Create Asset Type</h2>
                <Form className="admin-form">
                    <p className="admin-input-title">Asset details</p>

                    <input
                        className="admin-input"
                        placeholder="Asset name..."
                        onChange={(e) => setAssetName(e.target.value)}
                    ></input>
                </Form>
                {response
                    ? response.status === 'CONFLICT'
                        ? error(response.message, setResponse)
                        : success(setResponse, 'New Asset type created!')
                    : null}

                {assetName ? (
                    <Button
                        variant="secondary"
                        className="user-button "
                        onClick={(e) => {
                            createAsset(assetName, setResponse);
                            setId(Math.random().toString());
                        }}
                    >
                        Submit
                    </Button>
                ) : (
                    <Button variant="secondary" disabled className="user-button ">
                        Submit
                    </Button>
                )}
            </div>
            <div className="admin-card-tall">
                <h2 className="admin-card-title">Distribute Assets</h2>
                <Form className="admin-form">
                    <p className="admin-input-title">Placement details</p>

                    <div className="dropdown-container">
                        {' '}
                        <UnitDropdown
                            className="user-dropdown "
                            selectedUnit={addAssetUnit}
                            setUnit={setAddAssetUnit}
                            key={id}
                        />{' '}
                        <AssetDropdown
                            className="user-dropdown "
                            selectedAsset={addAssetName}
                            setAsset={setAddAssetName}
                            key={id + 1}
                        />
                    </div>
                </Form>
                <input
                    type="number"
                    className="admin-input topspace"
                    placeholder="Asset quantity..."
                    onChange={(e) => setAssetQuantity(e.target.value)}
                ></input>
                {addAssetResponse
                    ? addAssetResponse.status === 'BAD_REQUEST'
                        ? error(addAssetResponse.message, setAddAssetResponse)
                        : success(setAddAssetResponse, 'Asset distributed!')
                    : null}

                {assetQuantity && addAssetName != 'Select Asset' && addAssetUnit ? (
                    <Button
                        variant="secondary"
                        className="user-button "
                        onClick={() => {
                            addAssetToUnit(
                                addAssetName,
                                addAssetUnit,
                                assetQuantity,
                                setAddAssetResponse
                            );
                            forceUpdate();
                        }}
                    >
                        Submit
                    </Button>
                ) : (
                    <Button variant="secondary" disabled className="user-button ">
                        Submit
                    </Button>
                )}
            </div>
        </Container>
    );
};
