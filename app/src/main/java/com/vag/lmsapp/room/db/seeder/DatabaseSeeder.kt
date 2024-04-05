package com.vag.lmsapp.room.db.seeder

import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumWashType
import com.vag.lmsapp.room.db.MainDatabase
import com.vag.lmsapp.room.entities.EntityPackageExtras
import com.vag.lmsapp.room.entities.EntityPackageProduct
import com.vag.lmsapp.room.entities.EntityPackageService
import kotlinx.coroutines.*

class DatabaseSeeder(val db: MainDatabase) {
    fun run() {
        CoroutineScope(Job() + Dispatchers.Main).launch {
            runBlocking {
                UsersSeeder(db.daoUser()).seed()
                MachinesSeeder(db.daoMachine()).seed()
                DiscountsSeeder(db.daoDiscount()).seed()
                DeliveryProfileSeeder(db.daoDeliveryProfile()).seed()

                val washServices = WashServicesSeeder(db.daoWashService()).seed()
                val products = ProductsSeeder(db.daoProduct()).seed()
                val extras = ExtrasSeeder(db.daoExtras()).seed()
                val packages = PackageSeeder(db.daoJobOrderPackage()).seed()
                val packageServices: MutableList<EntityPackageService> = mutableListOf()
                val packageExtras: MutableList<EntityPackageExtras> = mutableListOf()
                val packageProducts: MutableList<EntityPackageProduct> = mutableListOf()


                val regularWash = washServices.find {it.name == "Warm Wash" && it.serviceRef.washType == EnumWashType.WARM && it.serviceRef.machineType == EnumMachineType.REGULAR_WASHER}
                val regularDry = washServices.find {it.name == "Regular Dry" && it.serviceRef.machineType == EnumMachineType.REGULAR_DRYER}
                val titanWash = washServices.find {it.name == "Warm Wash" && it.serviceRef.washType == EnumWashType.WARM && it.serviceRef.machineType == EnumMachineType.TITAN_WASHER}
                val titanDry = washServices.find {it.name == "Regular Dry" && it.serviceRef.machineType == EnumMachineType.TITAN_DRYER}

                val fold8kg = extras.find { it.name == "8KG Fold" }
                val fold12kg = extras.find { it.name == "12KG Fold" }
                val ariel = products.find { it.name == "Ariel" }
                val downy = products.find { it.name == "Downy" }

                packages.forEach {  _package ->
                    if(regularWash == null || regularDry == null || fold8kg == null || fold12kg == null || ariel == null || downy == null || titanWash == null || titanDry == null) return@forEach

                    if(_package.packageName == "Regular Package") {

                        packageServices.add(EntityPackageService(_package.id, regularWash.id, 1))
                        packageServices.add(EntityPackageService(_package.id, regularDry.id, 1))

                        packageExtras.add(EntityPackageExtras(_package.id, fold8kg.id, 1))

                        packageProducts.add(EntityPackageProduct(_package.id, ariel.id, 1f))
                        packageProducts.add(EntityPackageProduct(_package.id, downy.id, 1f))
                    } else if(_package.packageName == "Titan Package") {

                        packageServices.add(EntityPackageService(_package.id, titanWash.id, 1))
                        packageServices.add(EntityPackageService(_package.id, titanDry.id, 1))

                        packageExtras.add(EntityPackageExtras(_package.id, fold12kg.id, 1))

                        packageProducts.add(EntityPackageProduct(_package.id, ariel.id, 2f))
                        packageProducts.add(EntityPackageProduct(_package.id, downy.id, 2f))
                    }

                    db.daoJobOrderPackage().insertServices(packageServices)
                    db.daoJobOrderPackage().insertExtras(packageExtras)
                    db.daoJobOrderPackage().insertProducts(packageProducts)
                }
            }
        }
    }
}