import React, {useEffect, useState} from 'react';
import '../Overview/Overview.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Cookie from 'js-cookie';
import config from '../../config.json';

import {Dropdown, DropdownButton,} from 'react-bootstrap';

const fetchUnitData = async () => {
    let token = Cookie.get('token');
    const unitUrl = config.API_URL + '/organisational-unit';
    return fetch(unitUrl, {
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

export const UnitDropdown = (props) => {
    const [unitData, setUnitData] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchUnitData().then((data) => {
            setUnitData(data);
            setLoading(false);
        });
    }, []);

    if (!loading) {
        return (
            <DropdownButton
                variant="secondary"
                className="dropdown"
                id="dropdown-basic-button"
                title={props.selectedUnit}
            >
                {unitData.map((unit, index) => {
                    return (
                        <Dropdown.Item key={index} onClick={() => props.setUnit(unit.name)}>
                            {unit.name}
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
