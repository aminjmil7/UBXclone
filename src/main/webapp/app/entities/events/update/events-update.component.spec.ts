import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EventsFormService } from './events-form.service';
import { EventsService } from '../service/events.service';
import { IEvents } from '../events.model';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';

import { EventsUpdateComponent } from './events-update.component';

describe('Events Management Update Component', () => {
  let comp: EventsUpdateComponent;
  let fixture: ComponentFixture<EventsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eventsFormService: EventsFormService;
  let eventsService: EventsService;
  let parkService: ParkService;
  let equipementService: EquipementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EventsUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(EventsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EventsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventsFormService = TestBed.inject(EventsFormService);
    eventsService = TestBed.inject(EventsService);
    parkService = TestBed.inject(ParkService);
    equipementService = TestBed.inject(EquipementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Park query and add missing value', () => {
      const events: IEvents = { id: 456 };
      const park: IPark = { id: 41769 };
      events.park = park;

      const parkCollection: IPark[] = [{ id: 28925 }];
      jest.spyOn(parkService, 'query').mockReturnValue(of(new HttpResponse({ body: parkCollection })));
      const additionalParks = [park];
      const expectedCollection: IPark[] = [...additionalParks, ...parkCollection];
      jest.spyOn(parkService, 'addParkToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ events });
      comp.ngOnInit();

      expect(parkService.query).toHaveBeenCalled();
      expect(parkService.addParkToCollectionIfMissing).toHaveBeenCalledWith(
        parkCollection,
        ...additionalParks.map(expect.objectContaining)
      );
      expect(comp.parksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Equipement query and add missing value', () => {
      const events: IEvents = { id: 456 };
      const equipement: IEquipement = { id: 31206 };
      events.equipement = equipement;

      const equipementCollection: IEquipement[] = [{ id: 47255 }];
      jest.spyOn(equipementService, 'query').mockReturnValue(of(new HttpResponse({ body: equipementCollection })));
      const additionalEquipements = [equipement];
      const expectedCollection: IEquipement[] = [...additionalEquipements, ...equipementCollection];
      jest.spyOn(equipementService, 'addEquipementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ events });
      comp.ngOnInit();

      expect(equipementService.query).toHaveBeenCalled();
      expect(equipementService.addEquipementToCollectionIfMissing).toHaveBeenCalledWith(
        equipementCollection,
        ...additionalEquipements.map(expect.objectContaining)
      );
      expect(comp.equipementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const events: IEvents = { id: 456 };
      const park: IPark = { id: 73213 };
      events.park = park;
      const equipement: IEquipement = { id: 69838 };
      events.equipement = equipement;

      activatedRoute.data = of({ events });
      comp.ngOnInit();

      expect(comp.parksSharedCollection).toContain(park);
      expect(comp.equipementsSharedCollection).toContain(equipement);
      expect(comp.events).toEqual(events);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvents>>();
      const events = { id: 123 };
      jest.spyOn(eventsFormService, 'getEvents').mockReturnValue(events);
      jest.spyOn(eventsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ events });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: events }));
      saveSubject.complete();

      // THEN
      expect(eventsFormService.getEvents).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventsService.update).toHaveBeenCalledWith(expect.objectContaining(events));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvents>>();
      const events = { id: 123 };
      jest.spyOn(eventsFormService, 'getEvents').mockReturnValue({ id: null });
      jest.spyOn(eventsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ events: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: events }));
      saveSubject.complete();

      // THEN
      expect(eventsFormService.getEvents).toHaveBeenCalled();
      expect(eventsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvents>>();
      const events = { id: 123 };
      jest.spyOn(eventsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ events });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePark', () => {
      it('Should forward to parkService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(parkService, 'comparePark');
        comp.comparePark(entity, entity2);
        expect(parkService.comparePark).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEquipement', () => {
      it('Should forward to equipementService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(equipementService, 'compareEquipement');
        comp.compareEquipement(entity, entity2);
        expect(equipementService.compareEquipement).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
