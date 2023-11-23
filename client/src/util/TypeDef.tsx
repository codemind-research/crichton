export enum Status {
  NotStarted,
  Created,
  Testing,
  Tested,
}

export interface TestTypeInfo {
  name: string;
  selected: boolean;
  isTesting: boolean | undefined;
  isSuccess: boolean | undefined;
}

export interface WhiteBoxProject {
  Date: string;
  PrebuildTime: string;
  BuildTime: string;
  TestTime: string;
  Files: string;
  SuccessfulFiles: string;
  FailedFiles: string;
  Units: string;
  SuccessfulUnits: string;
  FailedUnits: string;
  TestCases: string;
  ExecutedLines: string;
  Lines: string;
  LineCoverage: string;
  ExecutedBranches: string;
  Branches: string;
  BranchCoverage: string;
  ExecutedPairs: string;
  Pairs: string;
  PairCoverage: string;
}

export interface WhiteBoxFile {
  Files: string;
  Status: string;
  ExecutedLines: string;
  Lines: string;
  LineCoverage: string;
  ExecutedBranches: string;
  Branches: string;
  BranchCoverage: string;
  ExecutedPairs: string;
  Pairs: string;
  PairCoverage: string;
}

export interface WhiteBoxFunc {
  FilePath: string;
  UnitNames: string;
  ExecutedLines: string;
  Lines: string;
  LineCoverage: string;
  ExecutedBranches: string;
  Branches: string;
  BranchCoverage: string;
  ExecutedPairs: string;
  Pairs: string;
  PairCoverage: string;
}
