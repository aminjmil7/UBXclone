import dayjs from 'dayjs/esm';

import { IPark, NewPark } from './park.model';

export const sampleWithRequiredData: IPark = {
  id: 53773,
};

export const sampleWithPartialData: IPark = {
  id: 55254,
  latitude: 14557,
  dateOpen: dayjs('2021-09-29T07:09'),
  dateClose: dayjs('2021-09-28T20:06'),
  note: 'Rubber Frozen',
};

export const sampleWithFullData: IPark = {
  id: 58024,
  parkName: 'defect',
  parkAddress: 'Ridge SMTP standardization',
  longtitude: 74825,
  latitude: 52243,
  verified: false,
  dateInstall: dayjs('2021-09-29T12:39'),
  dateOpen: dayjs('2021-09-28T12:43'),
  dateClose: dayjs('2021-09-28T16:38'),
  note: 'web-enabled',
  reseller: 'hardware Dynamic parse',
};

export const sampleWithNewData: NewPark = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
