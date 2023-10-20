import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRating, NewRating } from '../rating.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRating for edit and NewRatingFormGroupInput for create.
 */
type RatingFormGroupInput = IRating | PartialWithRequiredKeyOf<NewRating>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRating | NewRating> = Omit<T, 'ratingDate'> & {
  ratingDate?: string | null;
};

type RatingFormRawValue = FormValueOf<IRating>;

type NewRatingFormRawValue = FormValueOf<NewRating>;

type RatingFormDefaults = Pick<NewRating, 'id' | 'ratingDate'>;

type RatingFormGroupContent = {
  id: FormControl<RatingFormRawValue['id'] | NewRating['id']>;
  comment: FormControl<RatingFormRawValue['comment']>;
  ratingNumber: FormControl<RatingFormRawValue['ratingNumber']>;
  ratingDate: FormControl<RatingFormRawValue['ratingDate']>;
  user_id: FormControl<RatingFormRawValue['user_id']>;
  park: FormControl<RatingFormRawValue['park']>;
  equipement: FormControl<RatingFormRawValue['equipement']>;
};

export type RatingFormGroup = FormGroup<RatingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RatingFormService {
  createRatingFormGroup(rating: RatingFormGroupInput = { id: null }): RatingFormGroup {
    const ratingRawValue = this.convertRatingToRatingRawValue({
      ...this.getFormDefaults(),
      ...rating,
    });
    return new FormGroup<RatingFormGroupContent>({
      id: new FormControl(
        { value: ratingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      comment: new FormControl(ratingRawValue.comment),
      ratingNumber: new FormControl(ratingRawValue.ratingNumber),
      ratingDate: new FormControl(ratingRawValue.ratingDate),
      user_id: new FormControl(ratingRawValue.user_id),
      park: new FormControl(ratingRawValue.park),
      equipement: new FormControl(ratingRawValue.equipement),
    });
  }

  getRating(form: RatingFormGroup): IRating | NewRating {
    return this.convertRatingRawValueToRating(form.getRawValue() as RatingFormRawValue | NewRatingFormRawValue);
  }

  resetForm(form: RatingFormGroup, rating: RatingFormGroupInput): void {
    const ratingRawValue = this.convertRatingToRatingRawValue({ ...this.getFormDefaults(), ...rating });
    form.reset(
      {
        ...ratingRawValue,
        id: { value: ratingRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RatingFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      ratingDate: currentTime,
    };
  }

  private convertRatingRawValueToRating(rawRating: RatingFormRawValue | NewRatingFormRawValue): IRating | NewRating {
    return {
      ...rawRating,
      ratingDate: dayjs(rawRating.ratingDate, DATE_TIME_FORMAT),
    };
  }

  private convertRatingToRatingRawValue(
    rating: IRating | (Partial<NewRating> & RatingFormDefaults)
  ): RatingFormRawValue | PartialWithRequiredKeyOf<NewRatingFormRawValue> {
    return {
      ...rating,
      ratingDate: rating.ratingDate ? rating.ratingDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
