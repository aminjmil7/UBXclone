import dayjs from 'dayjs/esm';

import { IEvents, NewEvents } from './events.model';

export const sampleWithRequiredData: IEvents = {
  id: 89576,
};

export const sampleWithPartialData: IEvents = {
  id: 18562,
  eventDate: dayjs('2021-12-11T22:26'),
};

export const sampleWithFullData: IEvents = {
  id: 44653,
  eventName: 'back-end capacitor',
  eventDate: dayjs('2021-12-12T09:33'),
  user_id: 44243,
};

export const sampleWithNewData: NewEvents = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
