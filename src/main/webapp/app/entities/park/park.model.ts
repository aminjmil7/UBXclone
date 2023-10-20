import dayjs from 'dayjs/esm';

export interface IPark {
  id: number;
  parkName?: string | null;
  parkAddress?: string | null;
  longtitude?: number | null;
  latitude?: number | null;
  verified?: boolean | null;
  dateInstall?: dayjs.Dayjs | null;
  dateOpen?: dayjs.Dayjs | null;
  dateClose?: dayjs.Dayjs | null;
  note?: string | null;
  reseller?: string | null;
}

export type NewPark = Omit<IPark, 'id'> & { id: null };
