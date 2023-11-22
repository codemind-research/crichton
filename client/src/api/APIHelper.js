import Axios from "axios";
import AES from "../util/AES";

export default {
  POST_DATA_TOKEN: async function (url, data, token) {
    const decryptToken = await this.decryptToken(token);
    const config = {
      headers: { Authorization: decryptToken, "content-type": "application/json" },
      timeout: 1000000,
    };

    return await this.GetRestApi("POST", url, data, config);
  },
  POST_DATA_RESOURCE: async function (url, data, token) {
    const decryptToken = await this.decryptToken(token);
    const config = {
      headers: { Authorization: decryptToken },
      timeout: 1000000,
    };

    return await this.GetRestApi("POST", url, data, config);
  },

  GET_DATA: async function (url, token) {
    const decryptToken = await this.decryptToken(token);
    const config = {
      headers: { Authorization: decryptToken, "content-type": "application/json" },
      timeout: 10000000,
    };

    return await this.GetRestApi("GET", url, undefined, config);
  },

  GET_TOKEN: async function () {
    const config = {
      timeout: 1000000,
    };

    const response = await this.GetRestApi("GET", "/api/v1/crichton/auth/token", undefined, config);
    const accessToken = await this.encryptToken(response.result.accessToken);
    const refreshToken = await this.encryptToken(response.result.refreshToken);

    const result = {
      successful: response.successful,
      result: { accessToken: accessToken, refreshToken: refreshToken },
    };
    return result;
  },

  GetRestApi: async function (httpMethod, url, data, config) {
    let response = undefined;
    let result = undefined;

    while (response === undefined) {
      try {
        switch (httpMethod) {
          case "POST":
            response = await Axios.post(url, data, config);

            break;
          case "GET":
            response = await Axios.get(url, config);
            break;
          default:
            break;
        }
      } catch (error) {
        console.log(error.response);
        if (url.includes("token") || error.response.data.code.startsWith("F")) response = error.response;
        else
          switch (error.response.data.code) {
            case "T001":
              const refreshToken = window.sessionStorage.getItem("refreshToken");
              config.headers.RefreshToken = this.decryptToken(refreshToken);
              break;
            case "T002":
              const res = await this.GET_TOKEN();
              config.headers.Authorization = res.result.accessToken;
              break;
            default:
              response = error.reponse;
              break;
          }
      }
    }
    result = {
      successful: response.status ? response.status === 200 : false,
      result: response.data ? response.data : undefined,
    };

    return result;
  },

  encryptToken: async function (token) {
    return await AES.encrypt256(token);
  },
  decryptToken: async function (encryptedToken) {
    return await AES.decrypt256(encryptedToken);
  },
};
