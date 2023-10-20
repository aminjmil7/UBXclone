import { IPark } from 'app/entities/park/park.model';

export interface IEquipement {
  id: number;
  modelName?: string | null;
  modelNumber?: string | null;
  instruction?: string | null;
  verified?: boolean | null;
  park?: Pick<IPark, 'id'> | null;
}

export type NewEquipement = Omit<IEquipement, 'id'> & { id: null };
