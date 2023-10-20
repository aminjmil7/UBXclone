import dayjs from 'dayjs/esm';
import { IPark } from 'app/entities/park/park.model';
import { IEquipement } from 'app/entities/equipement/equipement.model';

export interface IEvents {
  id: number;
  eventName?: string | null;
  eventDate?: dayjs.Dayjs | null;
  user_id?: number | null;
  park?: Pick<IPark, 'id'> | null;
  equipement?: Pick<IEquipement, 'id'> | null;
}

export type NewEvents = Omit<IEvents, 'id'> & { id: null };
