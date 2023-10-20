import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPark, NewPark } from '../park.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPark for edit and NewParkFormGroupInput for create.
 */
type ParkFormGroupInput = IPark | PartialWithRequiredKeyOf<NewPark>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPark | NewPark> = Omit<T, 'dateInstall' | 'dateOpen' | 'dateClose'> & {
  dateInstall?: string | null;
  dateOpen?: string | null;
  dateClose?: string | null;
};

type ParkFormRawValue = FormValueOf<IPark>;

type NewParkFormRawValue = FormValueOf<NewPark>;

type ParkFormDefaults = Pick<NewPark, 'id' | 'verified' | 'dateInstall' | 'dateOpen' | 'dateClose'>;

type ParkFormGroupContent = {
  id: FormControl<ParkFormRawValue['id'] | NewPark['id']>;
  parkName: FormControl<ParkFormRawValue['parkName']>;
  parkAddress: FormControl<ParkFormRawValue['parkAddress']>;
  longtitude: FormControl<ParkFormRawValue['longtitude']>;
  latitude: FormControl<ParkFormRawValue['latitude']>;
  verified: FormControl<ParkFormRawValue['verified']>;
  dateInstall: FormControl<ParkFormRawValue['dateInstall']>;
  dateOpen: FormControl<ParkFormRawValue['dateOpen']>;
  dateClose: FormControl<ParkFormRawValue['dateClose']>;
  note: FormControl<ParkFormRawValue['note']>;
  reseller: FormControl<ParkFormRawValue['reseller']>;
};

export type ParkFormGroup = FormGroup<ParkFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ParkFormService {
  createParkFormGroup(park: ParkFormGroupInput = { id: null }): ParkFormGroup {
    const parkRawValue = this.convertParkToParkRawValue({
      ...this.getFormDefaults(),
      ...park,
    });
    return new FormGroup<ParkFormGroupContent>({
      id: new FormControl(
        { value: parkRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      parkName: new FormControl(parkRawValue.parkName),
      parkAddress: new FormControl(parkRawValue.parkAddress),
      longtitude: new FormControl(parkRawValue.longtitude),
      latitude: new FormControl(parkRawValue.latitude),
      verified: new FormControl(parkRawValue.verified),
      dateInstall: new FormControl(parkRawValue.dateInstall),
      dateOpen: new FormControl(parkRawValue.dateOpen),
      dateClose: new FormControl(parkRawValue.dateClose),
      note: new FormControl(parkRawValue.note),
      reseller: new FormControl(parkRawValue.reseller),
    });
  }

  getPark(form: ParkFormGroup): IPark | NewPark {
    return this.convertParkRawValueToPark(form.getRawValue() as ParkFormRawValue | NewParkFormRawValue);
  }

  resetForm(form: ParkFormGroup, park: ParkFormGroupInput): void {
    const parkRawValue = this.convertParkToParkRawValue({ ...this.getFormDefaults(), ...park });
    form.reset(
      {
        ...parkRawValue,
        id: { value: parkRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ParkFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      verified: false,
      dateInstall: currentTime,
      dateOpen: currentTime,
      dateClose: currentTime,
    };
  }

  private convertParkRawValueToPark(rawPark: ParkFormRawValue | NewParkFormRawValue): IPark | NewPark {
    return {
      ...rawPark,
      dateInstall: dayjs(rawPark.dateInstall, DATE_TIME_FORMAT),
      dateOpen: dayjs(rawPark.dateOpen, DATE_TIME_FORMAT),
      dateClose: dayjs(rawPark.dateClose, DATE_TIME_FORMAT),
    };
  }

  private convertParkToParkRawValue(
    park: IPark | (Partial<NewPark> & ParkFormDefaults)
  ): ParkFormRawValue | PartialWithRequiredKeyOf<NewParkFormRawValue> {
    return {
      ...park,
      dateInstall: park.dateInstall ? park.dateInstall.format(DATE_TIME_FORMAT) : undefined,
      dateOpen: park.dateOpen ? park.dateOpen.format(DATE_TIME_FORMAT) : undefined,
      dateClose: park.dateClose ? park.dateClose.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
