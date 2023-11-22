const crypto = require("crypto-browserify");

class CryptoManager {
  constructor() {
    this.crypto_key = "";
    this.iv = "";
  }

  setCryptoKey(keyData) {
    this.crypto_key = keyData;
    this.iv = keyData.substring(0, 16);
  }
}

const cryptoManager = new CryptoManager();

export default {
  generateCryptoKey: function () {
    const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*-_=+";
    let randomKey = "";

    for (let i = 0; i < 32; i++) {
      const randomIndex = Math.floor(Math.random() * characters.length);
      randomKey += characters.charAt(randomIndex);
    }

    cryptoManager.setCryptoKey(randomKey);
  },
  encrypt256: function (data) {
    if (data === undefined) return;
    const key = cryptoManager.crypto_key;
    const iv = cryptoManager.iv;

    const ciper = crypto.createCipheriv("aes-256-cbc", Buffer.from(key), iv);
    let encryptData = ciper.update(data, "utf8", "base64");
    encryptData += ciper.final("base64");
    return encryptData;
  },

  decrypt256: function (encryptedData) {
    const key = cryptoManager.crypto_key;
    const iv = cryptoManager.iv;

    const ciper = crypto.createDecipheriv("aes-256-cbc", key, iv);
    let decryptData = ciper.update(encryptedData, "base64", "utf8");
    decryptData += ciper.final("utf8");
    return decryptData;
  },
};
