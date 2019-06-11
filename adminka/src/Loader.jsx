import React, { Component } from 'react';
import { HashLoader } from 'react-spinners';
import { css } from '@emotion/core';

const override = css`
    display: block;
    margin: 0 auto;
    border-color: red;
    margin-top: 20%;
    width: 100%;
    height: 100%;
`;

export class Loader extends Component {
    render() {
        return (
            <div className='sweet-loading'>
              <HashLoader
                className={override}
                sizeUnit={"px"}
                size={200}
                color={'#019b0e'}
              />
            </div>
          ); 
    }
}