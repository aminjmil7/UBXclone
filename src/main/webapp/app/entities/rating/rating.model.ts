import dayjs from 'dayjs/esm';
import { IPark } from 'app/entities/park/park.model';
import { IEquipement } from 'app/entities/equipement/equipement.model';

export interface IRating {
  id: number;
  comment?: string | null;
  ratingNumber?: number | null;
  ratingDate?: dayjs.Dayjs | null;
  user_id?: number | null;
  park?: Pick<IPark, 'id'> | null;
  equipement?: Pick<IEquipement, 'id'> | null;
}

export type NewRating = Omit<IRating, 'id'> & { id: null };
