import Axios from "axios";

export default {
  POST_DATA_TOKEN: async function (url, port, data, token) {
    let config = {
      headers: { userauth: token, "content-type": "application/json" },
      timeout: 1000000,
    };

    return await this.GetRestApi("POST", url, port, data, config);
  },
  POST_DATA_RESOURCE: async function (url, port, data, token) {
    let config = {
      headers: { userauth: token },
      timeout: 1000000,
      responseType: "blob",
    };

    return await this.GetRestApi("POST", url, port, data, config);
  },

  GET_TOKEN: async function (url, port, token) {
    let config = {
      headers: { userauth: token },
      timeout: 10000000,
    };

    return await this.GetRestApi("GET", url, port, undefined, config);
  },

  GetRestApi: async function (httpMethod, url, port, data, config) {
    let response = undefined;
    let result = undefined;
    try {
      switch (httpMethod) {
        case "POST":
          response = await Axios.post(port + url, data, config);
          break;
        case "GET":
          response = await Axios.get(port + url, config);
          break;
        default:
          break;
      }
    } catch (error) {
      response = error.response;
    }

    if (response.status === 200) {
      result = { successful: true, status: response.status, result: response.data };
    } else {
      result = {
        successful: false,
        status: response.status,
        result: response.data ? response.data : undefined,
      };
    }

    return result;
  },
};
