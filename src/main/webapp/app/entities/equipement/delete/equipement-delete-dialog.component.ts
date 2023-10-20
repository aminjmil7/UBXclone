import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IEquipement } from '../equipement.model';
import { EquipementService } from '../service/equipement.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './equipement-delete-dialog.component.html',
})
export class EquipementDeleteDialogComponent {
  equipement?: IEquipement;

  constructor(protected equipementService: EquipementService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.equipementService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
