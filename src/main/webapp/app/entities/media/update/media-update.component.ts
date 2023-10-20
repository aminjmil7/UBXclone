import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { MediaFormService, MediaFormGroup } from './media-form.service';
import { IMedia } from '../media.model';
import { MediaService } from '../service/media.service';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';
import { IReport } from 'app/entities/report/report.model';
import { ReportService } from 'app/entities/report/service/report.service';
import { AuthType } from 'app/entities/enumerations/auth-type.model';

@Component({
  selector: 'jhi-media-update',
  templateUrl: './media-update.component.html',
})
export class MediaUpdateComponent implements OnInit {
  isSaving = false;
  media: IMedia | null = null;
  authTypeValues = Object.keys(AuthType);

  parksSharedCollection: IPark[] = [];
  equipementsSharedCollection: IEquipement[] = [];
  reportsSharedCollection: IReport[] = [];

  editForm: MediaFormGroup = this.mediaFormService.createMediaFormGroup();

  constructor(
    protected mediaService: MediaService,
    protected mediaFormService: MediaFormService,
    protected parkService: ParkService,
    protected equipementService: EquipementService,
    protected reportService: ReportService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePark = (o1: IPark | null, o2: IPark | null): boolean => this.parkService.comparePark(o1, o2);

  compareEquipement = (o1: IEquipement | null, o2: IEquipement | null): boolean => this.equipementService.compareEquipement(o1, o2);

  compareReport = (o1: IReport | null, o2: IReport | null): boolean => this.reportService.compareReport(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ media }) => {
      this.media = media;
      if (media) {
        this.updateForm(media);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const media = this.mediaFormService.getMedia(this.editForm);
    if (media.id !== null) {
      this.subscribeToSaveResponse(this.mediaService.update(media));
    } else {
      this.subscribeToSaveResponse(this.mediaService.create(media));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMedia>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(media: IMedia): void {
    this.media = media;
    this.mediaFormService.resetForm(this.editForm, media);

    this.parksSharedCollection = this.parkService.addParkToCollectionIfMissing<IPark>(this.parksSharedCollection, media.park);
    this.equipementsSharedCollection = this.equipementService.addEquipementToCollectionIfMissing<IEquipement>(
      this.equipementsSharedCollection,
      media.equipement
    );
    this.reportsSharedCollection = this.reportService.addReportToCollectionIfMissing<IReport>(this.reportsSharedCollection, media.report);
  }

  protected loadRelationshipsOptions(): void {
    this.parkService
      .query()
      .pipe(map((res: HttpResponse<IPark[]>) => res.body ?? []))
      .pipe(map((parks: IPark[]) => this.parkService.addParkToCollectionIfMissing<IPark>(parks, this.media?.park)))
      .subscribe((parks: IPark[]) => (this.parksSharedCollection = parks));

    this.equipementService
      .query()
      .pipe(map((res: HttpResponse<IEquipement[]>) => res.body ?? []))
      .pipe(
        map((equipements: IEquipement[]) =>
          this.equipementService.addEquipementToCollectionIfMissing<IEquipement>(equipements, this.media?.equipement)
        )
      )
      .subscribe((equipements: IEquipement[]) => (this.equipementsSharedCollection = equipements));

    this.reportService
      .query()
      .pipe(map((res: HttpResponse<IReport[]>) => res.body ?? []))
      .pipe(map((reports: IReport[]) => this.reportService.addReportToCollectionIfMissing<IReport>(reports, this.media?.report)))
      .subscribe((reports: IReport[]) => (this.reportsSharedCollection = reports));
  }
}
