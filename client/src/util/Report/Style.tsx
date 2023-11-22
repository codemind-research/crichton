export default function Style() {
  return `
    <style>
      * {
        color-adjust: exact;
        -webkit-print-color-adjust: exact;
        print-color-adjust: exact;
      }

      @media print, screen {
        * {
          color-adjust: exact;
          -webkit-print-color-adjust: exact;
          font-family: Arial, Helvetica, sans-serif;
        }
        .page {
          width: 210mm;
          height: 285mm;
          background: #fafafa;
          padding: 1cm;
          display: flex;
          flex-direction: column;
        }
        .title {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
        }
        #title.text {
          text-align: center;
          font-weight: bold;
          font-size: 50px;
          margin-top: 10px;
        }
        .proData {
          flex: 1;
          display: flex;
          justify-content: flex-end;
          align-items: flex-end;
          font-size: 20px;
          font-weight: bold;
          padding: 20px;
        }
        .testType {
          font-size: 30px;
          font-weight: bold;
          margin-bottom: 20px;
        }
        .table {
          font-size: 12px;
          border-collapse: collapse;
          margin-bottom: 50px;
        }
        .table td,
        .table th {
          border: 1px solid #ddd;
          padding: 8px;
        }
        .table th {
          padding-top: 12px;
          padding-bottom: 12px;
          text-align: left;
          background-color: #585858;
          color: white;
        }
        .page.extend {
          height: fit-content;
        }
        .fileinfo th.bulidError {
          background: #793131;
        }
        .fileinfo {
          border-collapse: collapse;
          width: 100%;
          margin-bottom: 20px;
        }
        .dirpath {
          opacity: 0.7;
        }

        .fileinfo th {
          padding: 10px 5px;
          background-color: #585858;
          color: white;
        }
        .fileinfo th,
        .fileinfo td {
          border: 1px solid #ddd;
          padding: 5px;
          text-align: center;
          font-size: 12px;
        }
        .fileinfo th:first-of-type,
        .fileinfo td:first-of-type {
          text-align: left !important;
        }
      }
      </style>
    `;
}
