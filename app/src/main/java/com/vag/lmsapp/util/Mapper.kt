package com.vag.lmsapp.util

interface Mapper<Entity, Domain> {
    fun mapFromEntity(entity: Entity) : Domain
    fun mapToEntity(domain: Domain) : Entity
}