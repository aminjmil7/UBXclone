import dayjs from 'dayjs/esm';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { IPark } from 'app/entities/park/park.model';

export interface IReport {
  id: number;
  mail?: string | null;
  message?: string | null;
  date?: dayjs.Dayjs | null;
  equipement?: Pick<IEquipement, 'id'> | null;
  park?: Pick<IPark, 'id'> | null;
}

export type NewReport = Omit<IReport, 'id'> & { id: null };
