delete from egbpa_usage eu where eu.suboccupancy in (select id from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'A'));

delete from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'A');

delete from egbpa_usage eu where eu.suboccupancy in (select id from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'F'));

delete from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'F');

delete from egbpa_usage eu where eu.suboccupancy in (select id from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'D'));

delete from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'D');

delete from egbpa_usage eu where eu.suboccupancy in (select id from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'H'));

delete from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'H');

delete from egbpa_usage eu where eu.suboccupancy in (select id from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'I'));

delete from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'I');


--egbpa_sub_occupancy table

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'A-R', 'RESIDENTIAL - Single Unit',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'A'),
    'RESIDENTIAL - Single Unit', 25
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'A-SR', 'RESIDENTIAL - Multi Unit',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'A'),
    'RESIDENTIAL - Multi Unit', 151
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'A-AF', 'Apartment/ Flat',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'A'),
    'Apartment/ Flat', 2
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'A-L', 'Lodge',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'A'),
    'Lodge', 5
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'A-SA', 'Shared Accommodation',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'A'),
    'Shared Accommodation', 193
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'F-CB', 'Commercial Building',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'F'),
    'Commercial Building', 30
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'F-O', 'Office',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'F'),
    'Office', 35
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'F-RT', 'Restaurant',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'F'),
    'Restaurant', 39
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'B-PN', 'Prenursery',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'B'),
    'Prenursery', 60
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'B-TA', 'Training Academy',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'B'),
    'Training Academy', 162
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'D-A', 'Assembly Buildings',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'D'),
    'Assembly Buildings', 72
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'D-C', 'Commercial Amenities',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'D'),
    'Commercial Amenities', 78
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'H-S', 'Storage Buildings',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'H'),
    'Storage Buildings', 12
);

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'I-H', 'Hazardous Buildings',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'I'),
    'Hazardous Buildings', 22
);


--egbpa_usage Table

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'A-R', 'RESIDENTIAL - Single Unit',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'A-R'),
    'RESIDENTIAL - Single Unit'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'A-SR', 'RESIDENTIAL - Multi Unit',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'A-SR'),
    'RESIDENTIAL - Multi Unit'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'A-AF', 'Apartment/ Flat',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'A-AF'),
    'Apartment/ Flat'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'A-L', 'Lodge',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'A-L'),
    'Lodge'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'A-SA', 'Shared Accommodation',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'A-SA'),
    'Shared Accommodation'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'F-CB', 'Commercial Building',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'F-CB'),
    'Commercial Building'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'F-O', 'Office',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'F-O'),
    'Office'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'F-RT', 'Restaurant',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'F-RT'),
    'Restaurant'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'B-PN', 'Prenursery',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'B-PN'),
    'Prenursery'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'B-TA', 'Training Academy',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'B-TA'),
    'Training Academy'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'D-A', 'Assembly Buildings',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'D-A'),
    'Assembly Buildings'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'D-C', 'Commercial Amenities',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'D-C'),
    'Commercial Amenities'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'H-S', 'Storage Buildings',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'H-S'),
    'Storage Buildings'
);

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'I-H', 'Hazardous Buildings',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'I-H'),
    'Hazardous Buildings'
);