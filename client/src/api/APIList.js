import APIHelper from "./APIHelper";

export default {
  // 폴더 경로 가져와서 압축하고 업로드
  uploadFile: async function (port, data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/", port, data, token);
  },
  // project 설정? 이건 따로 보내야되는 거임?
  setProjectSetting: async function (port, data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/", port, data, token);
  },
  // test 진행
  runTest: async function (port, data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/", port, data, token);
  },
  // log 가져오기
  getLogData: async function (port, token) {
    return await APIHelper.GET_TOKEN("/api/", port, token);
  },
  // report 정보 가져오기
  getReportData: async function (port, data, token) {
    return await APIHelper.POST_DATA_TOKEN("/api/", port, data, token);
  },
};
