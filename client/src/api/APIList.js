import APIHelper from "./APIHelper";

export default {
  // token 가져오기
  getToken: async function () {
    return await APIHelper.GET_TOKEN();
  },
  // plugin 목록 가져오기
  getPluginList: async function (token) {
    return await APIHelper.GET_DATA("/api/v1/crichton/test/plugin", token);
  },
  // 폴더 경로 가져와서 압축하고 업로드
  uploadFile: async function (data, token) {
    return await APIHelper.POST_DATA_RESOURCE("/api/v1/crichton/storage/upload", data, token);
  },
  // plugin test 진행
  runPluginTest: async function (data, token) {
    return await APIHelper.POST_DATA_RESOURCE("/api/v1/crichton/test/plugin/run", data, token);
  },
  // test progress(log) 가져오기
  getTestProgress: async function (data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/v1/crichton/test/log", data, token);
  },
  // report 정보 가져오기
  getReportData: async function (token) {
    return await APIHelper.GET_DATA("/api/v1/crichton/report/data", token);
  },
};
