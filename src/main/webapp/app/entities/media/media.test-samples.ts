import { AuthType } from 'app/entities/enumerations/auth-type.model';

import { IMedia, NewMedia } from './media.model';

export const sampleWithRequiredData: IMedia = {
  id: 70740,
};

export const sampleWithPartialData: IMedia = {
  id: 95019,
  filePath: 'Regional Dynamic Tuna',
  fileType: 'red paradigms Tactics',
  authType: AuthType['TECHFILE'],
};

export const sampleWithFullData: IMedia = {
  id: 21649,
  fileName: 'Devolved',
  filePath: 'interfaces EXE users',
  fileType: 'portals',
  authType: AuthType['LEARN'],
};

export const sampleWithNewData: NewMedia = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
