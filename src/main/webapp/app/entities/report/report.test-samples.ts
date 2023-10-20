import dayjs from 'dayjs/esm';

import { IReport, NewReport } from './report.model';

export const sampleWithRequiredData: IReport = {
  id: 35851,
};

export const sampleWithPartialData: IReport = {
  id: 7882,
  mail: 'Cambridgeshire payment',
  message: 'content-based cross-platform mobile',
};

export const sampleWithFullData: IReport = {
  id: 87024,
  mail: 'array',
  message: 'Account extensible',
  date: dayjs('2021-09-28T20:36'),
};

export const sampleWithNewData: NewReport = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
