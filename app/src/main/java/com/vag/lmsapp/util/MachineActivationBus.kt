package com.vag.lmsapp.util

import com.vag.lmsapp.model.MachineActivationQueues
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MachineActivationBus

@Inject
constructor()
{
    private val _queues: MutableList<MachineActivationQueues> = mutableListOf()

    fun add(queues: MachineActivationQueues) {
        if(_queues.find {
            queues.machineId == it.machineId
        } == null) {
            _queues.add(queues)
        }
    }

    fun remove(machineId: UUID?) {
        println("trying to remove item")
        _queues.find {
            it.machineId == machineId
        }.let {
            _queues.remove(it)
            println("queue removed")
        }
    }

    fun get(machineId: UUID?) : MachineActivationQueues? {
        return _queues.find { it.machineId == machineId }
    }

    fun size() : Int {
        return _queues.size
    }
}