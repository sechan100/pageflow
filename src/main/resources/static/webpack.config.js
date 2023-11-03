const path = require('path');

module.exports = {
  entry: '/react/src/book/pageOutline.js',
  output: {
    filename: 'pageOutlineBundle.js',
    path: path.resolve(__dirname, './react/dist')
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-react']
          }
        }
      }
    ]
  }
};
