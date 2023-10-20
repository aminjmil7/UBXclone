import { IPark } from 'app/entities/park/park.model';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { IReport } from 'app/entities/report/report.model';
import { AuthType } from 'app/entities/enumerations/auth-type.model';

export interface IMedia {
  id: number;
  fileName?: string | null;
  filePath?: string | null;
  fileType?: string | null;
  authType?: AuthType | null;
  park?: Pick<IPark, 'id'> | null;
  equipement?: Pick<IEquipement, 'id'> | null;
  report?: Pick<IReport, 'id'> | null;
}

export type NewMedia = Omit<IMedia, 'id'> & { id: null };
