import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEvents, NewEvents } from '../events.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEvents for edit and NewEventsFormGroupInput for create.
 */
type EventsFormGroupInput = IEvents | PartialWithRequiredKeyOf<NewEvents>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEvents | NewEvents> = Omit<T, 'eventDate'> & {
  eventDate?: string | null;
};

type EventsFormRawValue = FormValueOf<IEvents>;

type NewEventsFormRawValue = FormValueOf<NewEvents>;

type EventsFormDefaults = Pick<NewEvents, 'id' | 'eventDate'>;

type EventsFormGroupContent = {
  id: FormControl<EventsFormRawValue['id'] | NewEvents['id']>;
  eventName: FormControl<EventsFormRawValue['eventName']>;
  eventDate: FormControl<EventsFormRawValue['eventDate']>;
  user_id: FormControl<EventsFormRawValue['user_id']>;
  park: FormControl<EventsFormRawValue['park']>;
  equipement: FormControl<EventsFormRawValue['equipement']>;
};

export type EventsFormGroup = FormGroup<EventsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventsFormService {
  createEventsFormGroup(events: EventsFormGroupInput = { id: null }): EventsFormGroup {
    const eventsRawValue = this.convertEventsToEventsRawValue({
      ...this.getFormDefaults(),
      ...events,
    });
    return new FormGroup<EventsFormGroupContent>({
      id: new FormControl(
        { value: eventsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      eventName: new FormControl(eventsRawValue.eventName),
      eventDate: new FormControl(eventsRawValue.eventDate),
      user_id: new FormControl(eventsRawValue.user_id),
      park: new FormControl(eventsRawValue.park),
      equipement: new FormControl(eventsRawValue.equipement),
    });
  }

  getEvents(form: EventsFormGroup): IEvents | NewEvents {
    return this.convertEventsRawValueToEvents(form.getRawValue() as EventsFormRawValue | NewEventsFormRawValue);
  }

  resetForm(form: EventsFormGroup, events: EventsFormGroupInput): void {
    const eventsRawValue = this.convertEventsToEventsRawValue({ ...this.getFormDefaults(), ...events });
    form.reset(
      {
        ...eventsRawValue,
        id: { value: eventsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EventsFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      eventDate: currentTime,
    };
  }

  private convertEventsRawValueToEvents(rawEvents: EventsFormRawValue | NewEventsFormRawValue): IEvents | NewEvents {
    return {
      ...rawEvents,
      eventDate: dayjs(rawEvents.eventDate, DATE_TIME_FORMAT),
    };
  }

  private convertEventsToEventsRawValue(
    events: IEvents | (Partial<NewEvents> & EventsFormDefaults)
  ): EventsFormRawValue | PartialWithRequiredKeyOf<NewEventsFormRawValue> {
    return {
      ...events,
      eventDate: events.eventDate ? events.eventDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
