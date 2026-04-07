package com.zekiloni.george.provisioning.domain.catalog.model.specification;

import com.zekiloni.george.provisioning.domain.catalog.model.characteristic.CharacteristicSpecification;

import java.util.List;

public class ServiceSpecification {
    private String name;
    private String description;
    private String version;
    private List<CharacteristicSpecification> characteristicSpecification;

    public List<CharacteristicSpecification> getCharacteristicSpecification() {
        if (characteristicSpecification == null) {
            characteristicSpecification = List.of();
        }
        return characteristicSpecification;
    }

    public void addCharacteristicSpecification(CharacteristicSpecification characteristicSpecification) {
        this.characteristicSpecification.add(characteristicSpecification);
    }
}
