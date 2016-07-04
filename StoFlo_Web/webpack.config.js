const webpack = require('webpack')
const path = require('path')
const TransferWebpackPlugin = require('transfer-webpack-plugin')

const config = {
  entry: [path.join(__dirname, 'app/app.js')],
  resolve: {
    extensions: ["", ".js"],
    node_modules: ["node_modules"]
  },
  output: {
    path: path.resolve(__dirname, 'build'),
    filename: 'app.js'
  },
  plugins: [
    new webpack.optimize.UglifyJsPlugin({
      compress: { warnings: false }
    }),
    new webpack.NoErrorsPlugin(),
    new TransferWebpackPlugin([
      {from: 'www'}
    ], __dirname)
  ],
  module: {
    loaders: [
      {
        test: /\.js$/,
        loader: 'babel',
        exclude: [path.resolve(__dirname, 'node_modules')],
        query: {
          "presets": ["es2015", "react"]
        }
      }
    ]
  }
}

module.exports = config
