FROM upyogio/alpine-node-builder-14:yarn AS build
#FROM ghcr.io/egovernments/alpine-node-builder-14:yarn AS build
RUN apk update && apk upgrade
RUN apk add --no-cache git>2.30.0
RUN yarn add @upyog/digit-ui-libraries@3.0.1
RUN yarn add @upyog/digit-ui-module-engagement@3.0.3
RUN yarn add @upyog/digit-ui-module-hrms@3.0.1
RUN yarn add @upyog/digit-ui-module-core@3.0.3
RUN yarn add @upyog/digit-ui-module-common@3.0.5
RUN yarn add @upyog/digit-ui-module-bills@3.0.2
RUN yarn add @nudmcdgnpm/digit-ui-react-components@1.2.14
RUN yarn add @nudmcdgnpm/upyog-ui-module-obpsv2
ARG WORK_DIR
WORKDIR /app
ENV NODE_OPTIONS "--max-old-space-size=16384"
ENV GENERATE_SOURCEMAP "false"

COPY ${WORK_DIR} .
RUN ls -lah

#RUN node web/envs.js
RUN cd web/ \
    && node envs.js \
    && node -e 'console.log(v8.getHeapStatistics().heap_size_limit/(16384*16384))' \
    &&  ./install-deps.sh \
    && yarn install \
    && yarn build 
    

FROM nginx:mainline-alpine
#FROM ghcr.io/egovernments/nginx:mainline-alpine
ENV WORK_DIR=/var/web/upyog-ui

RUN mkdir -p ${WORK_DIR}

COPY --from=build /app/web/build ${WORK_DIR}/
COPY --from=build /app/web/docker/nginx.conf /etc/nginx/conf.d/default.conf
