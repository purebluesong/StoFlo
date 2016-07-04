const webpack = require('webpack')
const path = require('path')
const TransferWebpackPlugin = require('transfer-webpack-plugin')

const rel = x => path.resolve(__dirname, x[0])

const config = {
  entry: {
    profile: rel`profile/entry.js`,
    play:    rel`play/entry.js`,
    create:  rel`create/entry.js`
  },
  resolve: {
    extensions: ['', '.js'],
    node_modules: ['node_modules']
  },
  output: {
    path: 'build',
    filename: '[name].js'
  },
  plugins: [
    new webpack.NoErrorsPlugin(),
    new TransferWebpackPlugin([
      {from: 'assets'}
    ], __dirname)
  ],
  module: {
    loaders: [
      {
        test: /\.js$/,
        loader: 'babel',
        exclude: ['node_modules'],
        query: {
          presets: ['es2015', 'react']
        }
      }
    ]
  }
}

module.exports = config
