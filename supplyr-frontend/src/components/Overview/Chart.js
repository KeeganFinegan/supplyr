import {Line} from 'react-chartjs-2';
import React from 'react';
import './Chart.css';
import dateFormat from 'dateformat';

export const Chart = (props) => {
    let dataset = props.tradeData.map((obj, index) => {
        if (obj.type === 'SELL') {
            return obj.price;
        }
    });

    let labels = props.tradeData.map((obj, index) => {
        if (obj.type === 'SELL') {
            return dateFormat(obj.timestamp, 'mm/yyyy');
        }
    });

    // Format data for use with Chart.js
    let data = {
        // Reverse labels array to be in the correct order
        labels: labels,

        datasets: [
            {
                fill: false,
                tension: 0.1,
                label: props.asset + ' Sale Price',
                fillColor: [
                    'rgba(0,10,220,0.5)',
                    'rgba(220,0,10,0.5)',
                    'rgba(220,0,0,0.5)',
                    'rgba(120,250,120,0.5)',
                ],
                backgroundColor: 'rgba(50, 166, 255, 0.62) ',
                data: dataset,

                borderColor: 'rgba(50, 166, 255, 0.62) ',
                scaleFontColor: 'rgba(50, 166, 255, 1) ',
                legend: {
                    display: false,
                },
            },
        ],
    };
    const options = {
        borderWidth: 3,
        spanGaps: true,
        responsive: true,
        scales: {
            yAxis: [
                {
                    type: 'time',
                    display: true,
                },
            ],
        },
    };

    return (
        <div className="chart-container">
            <Line options={options} data={data}/>
        </div>
    );
};
