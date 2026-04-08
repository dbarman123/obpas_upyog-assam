UPDATE egbpa_occupancy SET "name"='Institutional', description='Institutional' WHERE code='B';
UPDATE egbpa_occupancy SET "name"='Heath Facilities', description='Heath Facilities' WHERE code='C';
UPDATE egbpa_occupancy set "name"='Commercial', description='Commercial', colorcode=null where code='F';

delete from egbpa_usage eu where eu.suboccupancy in (select id from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'B'));

delete from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'B');

delete from egbpa_usage eu where eu.suboccupancy in (select id from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'C'));

delete from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'C');

delete from egbpa_usage eu where eu.suboccupancy in (select id from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'G'));

delete from egbpa_sub_occupancy eso where eso.occupancy in (select id from egbpa_occupancy eo where code = 'G');

--egbpa_sub_occupancy table

INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'B-NS', 'PreNursery/Nursery Schools',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'B'),
    'PreNursery/Nursery Schools', 80
);


INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'B-PS', 'Primary school',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'B'),
    'Primary school', 40
);


INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'B-HEI', 'Higher Educational Institute',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'B'),
    'Higher Educational Institute', 15
);


INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'B-C', 'College',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'B'),
    'College', 4
);


INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'C-H', 'Hospitals',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'C'),
    'Hospitals', 20
);


INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'C-C', 'Clinics',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'C'),
    'Clinics', 21
);


INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'G-L', 'Light',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'G'),
    'Light', 10
);


INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'G-M', 'Medium',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'G'),
    'Medium', 33
);


INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'G-F', 'Flatted',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'G'),
    'Flatted', 34
);


INSERT INTO egbpa_sub_occupancy (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, maxfar, occupancy,
    description, colorcode
) VALUES 
(
    nextval('seq_egbpa_sub_occupancy'), 'G-SF', 'Standalone Factory',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_sub_occupancy), 't', 1, NOW(), NOW(),
    1, 0, 0.5, (SELECT id FROM egbpa_occupancy WHERE code = 'G'),
    'Standalone Factory', 9
);

--egbpa_usage Table

INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'B-NS', 'PreNursery/ Nursery Schools',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'B-NS'),
    'PreNursery/ Nursery Schools'
);


INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'B-PS', 'Primary school',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'B-PS'),
    'Primary school'
);


INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'B-HEI', 'Higher Educational Institute',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'B-HEI'),
    'Higher Educational Institute'
);


INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'B-C', 'College',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'B-C'),
    'College'
);


INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'C-H', 'Hospitals',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'C-H'),
    'Hospitals'
);


INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'C-C', 'Clinics',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'C-C'),
    'Clinics'
);


INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'G-L', 'Light',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'G-L'),
    'Light'
);


INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'G-M', 'Medium',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'G-M'),
    'Medium'
);


INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'G-F', 'Flatted',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'G-F'),
    'Flatted'
);


INSERT INTO egbpa_usage (
    id, code, name, ordernumber, isactive, createdby, createddate,
    lastmodifieddate, lastmodifiedby, version, suboccupancy,
    description
) VALUES 
(
    nextval('seq_egbpa_usage'), 'G-SF', 'Standalone Factory',
    (SELECT MAX(ordernumber) + 1 FROM egbpa_usage), 't', 1, NOW(), NOW(),
    1, 0, (SELECT id FROM egbpa_sub_occupancy WHERE code = 'G-SF'),
    'Standalone Factory'
);