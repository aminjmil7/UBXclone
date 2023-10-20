import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEquipement, NewEquipement } from '../equipement.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEquipement for edit and NewEquipementFormGroupInput for create.
 */
type EquipementFormGroupInput = IEquipement | PartialWithRequiredKeyOf<NewEquipement>;

type EquipementFormDefaults = Pick<NewEquipement, 'id' | 'verified'>;

type EquipementFormGroupContent = {
  id: FormControl<IEquipement['id'] | NewEquipement['id']>;
  modelName: FormControl<IEquipement['modelName']>;
  modelNumber: FormControl<IEquipement['modelNumber']>;
  instruction: FormControl<IEquipement['instruction']>;
  verified: FormControl<IEquipement['verified']>;
  park: FormControl<IEquipement['park']>;
};

export type EquipementFormGroup = FormGroup<EquipementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EquipementFormService {
  createEquipementFormGroup(equipement: EquipementFormGroupInput = { id: null }): EquipementFormGroup {
    const equipementRawValue = {
      ...this.getFormDefaults(),
      ...equipement,
    };
    return new FormGroup<EquipementFormGroupContent>({
      id: new FormControl(
        { value: equipementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      modelName: new FormControl(equipementRawValue.modelName),
      modelNumber: new FormControl(equipementRawValue.modelNumber),
      instruction: new FormControl(equipementRawValue.instruction),
      verified: new FormControl(equipementRawValue.verified),
      park: new FormControl(equipementRawValue.park),
    });
  }

  getEquipement(form: EquipementFormGroup): IEquipement | NewEquipement {
    return form.getRawValue() as IEquipement | NewEquipement;
  }

  resetForm(form: EquipementFormGroup, equipement: EquipementFormGroupInput): void {
    const equipementRawValue = { ...this.getFormDefaults(), ...equipement };
    form.reset(
      {
        ...equipementRawValue,
        id: { value: equipementRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EquipementFormDefaults {
    return {
      id: null,
      verified: false,
    };
  }
}
