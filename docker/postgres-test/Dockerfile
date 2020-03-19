FROM postgres:12

LABEL maintainer="true.cp@gmail.com" tag="postgres-test"

#ENV POSTGRES_PASSWORD postgres
ENV POSTGRES_HOST_AUTH_METHOD trust

ENTRYPOINT docker-entrypoint.sh -c 'max_prepared_transactions=100'