import APIHelper from "./APIHelper";

export default {
  // token 가져오기
  getToken: async function () {
    return await APIHelper.GET_TOKEN();
  },
  // 폴더 경로 가져와서 압축하고 업로드
  uploadFile: async function (data, token) {
    return await APIHelper.POST_DATA_RESOURCE("/api/v1/crichton/storage/upload", data, token);
  },
  // whitebox test 진행
  runWhiteboxTest: async function (data, token) {
    return await APIHelper.POST_DATA_RESOURCE("/api/v1/crichton/test/unit/run", data, token);
  },
  // 결함주입 test 진행
  runInjectionTest: async function (data, token) {
    return await APIHelper.POST_DATA_RESOURCE("/api/v1/crichton/test/injection/run", data, token);
  },
  // test progress(log) 가져오기
  getTestProgress: async function (token) {
    return await APIHelper.GET_DATA("/api/v1/crichton/test/progress", token);
  },
  // report 정보 가져오기
  getReportData: async function (data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/v1/crichton/report/data", data, token);
  },
};
