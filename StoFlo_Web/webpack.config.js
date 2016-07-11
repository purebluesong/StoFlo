const webpack = require('webpack')
const path = require('path')
const CopyWebpackPlugin = require('copy-webpack-plugin')

const TARGET = process.env.npm_lifecycle_event || 'build'

const entry = TARGET == 'build' ?
    x => path.resolve(__dirname, x[0]) :
    x => ['webpack/hot/dev-server', 'webpack/hot/only-dev-server', path.resolve(__dirname, x[0])]

const babelConfig = {
    presets: ['es2015', 'react'],
    plugins: [
        'transform-class-properties',
        'transform-function-bind',
        'transform-object-rest-spread'
    ]
}

module.exports = {
    entry: {
        profile: entry`profile/entry.js`,
        play:    entry`play/entry.js`,
        create:  entry`create/entry.js`
    },
    devServer: {
        contentBase: 'assets',
        hot: true,
        port: 3000,
        host: '0.0.0.0',
        inline: true
    },
    resolve: {
        extensions: ['', '.js'],
        node_modules: ['node_modules']
    },
    output: {
        path: 'build',
        filename: '[name].js'
    },
    devtool: TARGET == 'build' ? 'source-map' : 'cheap-module-eval-source-map',
    plugins: TARGET == 'build' ? [
        new webpack.optimize.UglifyJsPlugin({compress: {warnings: false}}),
        new CopyWebpackPlugin([{from: 'assets'}])
    ] : [
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NoErrorsPlugin()
    ],
    module: {
        loaders: [
            {
                test: /\.js$/,
                loaders: TARGET == 'build' ?
                    ['babel?'+JSON.stringify(babelConfig)] :
                    ['react-hot', 'babel?'+JSON.stringify(babelConfig)],
                exclude: /node_modules/
            }
        ]
    }
}

