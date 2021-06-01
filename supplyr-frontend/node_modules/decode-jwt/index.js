#! /usr/bin/env node
const argv = require('yargs').argv

const err = () => console.log(`>>>>>[decode-jwt]: please input your jwt.`)

const atob = function (str) {
  return Buffer.from(str, 'base64').toString('binary');
}

const decode = function () {
  if (!argv._.length) {
    return err()
  }
  const [token] = argv._
  try {
    const body = atob(token.split('.')[1])
    return body
  } catch (error) {
    console.log(error)
  }
}

if (require.main === module) {
  const out = decode()
  console.log('\n>>>>[decode-jwt]: \n'+out)
}