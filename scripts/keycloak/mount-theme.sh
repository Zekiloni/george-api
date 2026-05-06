

### mount keycloak theme to container named keycloak, theme is located in directory above the current directory
# check if the container is running
if [ "$(docker ps -q -f name=keycloak)" ]; then
    echo "Container keycloak is running, mounting theme..."
    docker cp ../theme keycloak:/opt/keycloak/themes/
    echo "Theme mounted successfully."
else
    echo "Container keycloak is not running, please start the container first."
fi