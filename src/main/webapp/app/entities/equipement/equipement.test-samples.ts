import { IEquipement, NewEquipement } from './equipement.model';

export const sampleWithRequiredData: IEquipement = {
  id: 42322,
};

export const sampleWithPartialData: IEquipement = {
  id: 51907,
  instruction: 'open-source Refined',
  verified: false,
};

export const sampleWithFullData: IEquipement = {
  id: 32375,
  modelName: 'Shoes Account Faso',
  modelNumber: 'Loan',
  instruction: 'human-resource Vision-oriented multi-byte',
  verified: true,
};

export const sampleWithNewData: NewEquipement = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
