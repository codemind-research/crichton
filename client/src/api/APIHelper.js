import Axios from "axios";

const port = "https://localhost:9090";

export default {
  POST_DATA_TOKEN: async function (url, data, token) {
    const config = {
      headers: { userauth: token, "content-type": "application/json" },
      timeout: 1000000,
    };

    return await this.GetRestApi("POST", url, data, config);
  },
  POST_DATA_RESOURCE: async function (url, data, token) {
    const config = {
      headers: { userauth: token },
      timeout: 1000000,
      responseType: "blob",
    };

    return await this.GetRestApi("POST", url, data, config);
  },

  GET_TOKEN: async function (url, token) {
    const config = {
      headers: { userauth: token, "content-type": "application/json" },
      timeout: 10000000,
    };

    return await this.GetRestApi("GET", url, undefined, config);
  },

  GetRestApi: async function (httpMethod, url, data, config) {
    let response = undefined;
    let result = undefined;

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
      response = error.response;
    }

    result = {
      successful: response.status === 200,
      result: response.data ? response.data : undefined,
    };

    return result;
  },
};
