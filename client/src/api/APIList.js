import APIHelper from "./APIHelper";

export default {
  // token 보내기
  getToken: async function () {
    return await APIHelper.GET_TOKEN("/api/v1/crichton/auth/token", "");
  },
  // 폴더 경로 가져와서 압축하고 업로드
  uploadFile: async function (data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/v1/crichton/storage/upload", data, token);
  },
  // test 진행
  runTest: async function (data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/v1/crichton/test/run", data, token);
  },
  // test progress
  getTestProgress: async function (data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/v1/crichton/test/progress", data, token);
  },
  // log 가져오기
  getLogData: async function (token) {
    return await APIHelper.GET_TOKEN("/api/", token);
  },
  // report 정보 가져오기
  getReportData: async function (data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/", data, token);
  },
};
