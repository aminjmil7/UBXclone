import dayjs from 'dayjs/esm';

import { IRating, NewRating } from './rating.model';

export const sampleWithRequiredData: IRating = {
  id: 92458,
};

export const sampleWithPartialData: IRating = {
  id: 33071,
  comment: 'Infrastructure',
  ratingDate: dayjs('2022-02-02T04:43'),
};

export const sampleWithFullData: IRating = {
  id: 96232,
  comment: 'Intelligent',
  ratingNumber: 16539,
  ratingDate: dayjs('2022-02-02T07:01'),
  user_id: 78536,
};

export const sampleWithNewData: NewRating = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
