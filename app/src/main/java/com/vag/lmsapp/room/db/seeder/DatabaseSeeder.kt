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


                val regularWash = washServices.find {it.name == "Warm Wash" && it.serviceRef.washType == EnumWashType.WARM && it.serviceRef.machineType == EnumMachineType.REGULAR}
                val regularDry = washServices.find {it.name == "Regular Dry" && it.serviceRef.machineType == EnumMachineType.REGULAR}
                val titanWash = washServices.find {it.name == "Warm Wash" && it.serviceRef.washType == EnumWashType.WARM && it.serviceRef.machineType == EnumMachineType.TITAN}
                val titanDry = washServices.find {it.name == "Regular Dry" && it.serviceRef.machineType == EnumMachineType.TITAN}

                val fold8kg = extras.find { it.name == "8KG Fold" }
                val fold12kg = extras.find { it.name == "12KG Fold" }
                val ariel = products.find { it.name == "Ariel" }
                val downy = products.find { it.name == "Downy" }

                packages.forEach {  _package ->
                    if(regularWash == null || regularDry == null || fold8kg == null || fold12kg == null || ariel == null || downy == null || titanWash == null || titanDry == null) return@forEach

                    if(_package.packageName == "Regular Package") {

                        packageServices.add(EntityPackageService(
                            _package.id,
                            regularWash.id,
                            regularWash.name,
                            regularWash.serviceRef,
                            regularWash.price,
                            1
                        ))
                        packageServices.add(EntityPackageService(
                            _package.id,
                            regularDry.id,
                            regularDry.name,
                            regularDry.serviceRef,
                            regularDry.price,
                            1
                        ))

                        packageExtras.add(EntityPackageExtras(
                            _package.id,
                            fold8kg.id,
                            fold8kg.name,
                            fold8kg.category,
                            1,
                            fold8kg.price
                        ))

                        packageProducts.add(EntityPackageProduct(
                            _package.id,
                            ariel.id,
                            ariel.name,
                            ariel.measureUnit,
                            ariel.unitPerServe,
                            ariel.price,
                            ariel.productType,
                            1))
                        packageProducts.add(EntityPackageProduct(
                            _package.id,
                            downy.id,
                            downy.name,
                            downy.measureUnit,
                            downy.unitPerServe,
                            downy.price,
                            downy.productType,
                            1))
                    } else if(_package.packageName == "Titan Package") {

                        packageServices.add(EntityPackageService(
                            _package.id,
                            titanWash.id,
                            titanWash.name,
                            titanWash.serviceRef,
                            titanWash.price,
                            1
                        ))
                        packageServices.add(EntityPackageService(
                            _package.id,
                            titanDry.id,
                            titanDry.name,
                            titanDry.serviceRef,
                            titanDry.price,
                            1
                        ))

                        packageExtras.add(EntityPackageExtras(
                            _package.id,
                            fold12kg.id,
                            fold12kg.name,
                            fold12kg.category,
                            1,
                            fold12kg.price
                        ))

                        packageProducts.add(EntityPackageProduct(_package.id,
                            ariel.id,
                            ariel.name,
                            ariel.measureUnit,
                            ariel.unitPerServe,
                            ariel.price,
                            ariel.productType,
                            2
                        ))
                        packageProducts.add(EntityPackageProduct(
                            _package.id,
                            downy.id,
                            downy.name,
                            downy.measureUnit,
                            downy.unitPerServe,
                            downy.price,
                            downy.productType,
                            2
                        ))
                    }

                    db.daoJobOrderPackage().insertServices(packageServices)
                    db.daoJobOrderPackage().insertExtras(packageExtras)
                    db.daoJobOrderPackage().insertProducts(packageProducts)
                }
            }
        }
    }
}