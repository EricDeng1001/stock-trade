kubectl label ns default istio-injection=enabled
kubectl label no trade-worker location=central
kubectl label no trade-worker2 location=mock-mock-user
kubectl label no trade-worker3 location=mock-mock-user2

docker exec trade-worker mkdir -p //mnt/data
docker exec trade-worker2 mkdir -p //mnt/data
docker exec trade-worker3 mkdir -p //mnt/data

kind load docker-image trade-mock:1.0-SNAPSHOT --name trade &
kind load docker-image trade-empty:1.0-SNAPSHOT --name trade &
{
  docker pull mysql:8
  kind load docker-image mysql:8 --name trade
} &
{
  docker pull istio/proxyv2:1.9.2
  kind load docker-image istio/proxyv2:1.9.2 --name trade
} &

docker pull istio/pilot:1.9.2
kind load docker-image istio/pilot:1.9.2 --name trade

istioctl install -y -f istio.yml &