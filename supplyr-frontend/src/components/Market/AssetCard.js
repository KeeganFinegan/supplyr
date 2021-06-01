import React from 'react';
import './AssetCard.css';
import { Col } from 'react-bootstrap';

export default function AssetCard(props) {
  const assetData = props.assetData;
  return <Col className="ac-column">{assetData.name}</Col>;
}
