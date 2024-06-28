package com.vag.lmsapp.app.joborders.create.packages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vag.lmsapp.app.joborders.create.delivery.DeliveryCharge
import com.vag.lmsapp.app.joborders.create.discount.MenuDiscount
import com.vag.lmsapp.app.joborders.create.extras.MenuExtrasItem
import com.vag.lmsapp.app.joborders.create.products.MenuProductItem
import com.vag.lmsapp.app.joborders.create.services.MenuServiceItem
import com.vag.lmsapp.app.joborders.create.shared_ui.QuantityModel
import com.vag.lmsapp.room.entities.EntityPackageExtrasWithExtras
import com.vag.lmsapp.room.entities.EntityPackageProductWithProduct
import com.vag.lmsapp.room.entities.EntityPackageServiceWithService
import com.vag.lmsapp.room.repository.JobOrderPackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AvailablePackageViewModel
@Inject
constructor(
    private val repository: JobOrderPackageRepository
): ViewModel() {
    private val _availablePackages = MutableLiveData<List<MenuJobOrderPackage>>()
    val availablePackages /*= repository.getAllAsLiveData() */: LiveData<List<MenuJobOrderPackage>> = _availablePackages

    private val _dataState = MutableLiveData<DataState>()
    val dataState: LiveData<DataState> = _dataState

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            _availablePackages.value = repository.getAll("")
        }
    }

    fun setPreselectedPackages(packages: List<MenuJobOrderPackage>?) {
        packages?.forEach { mjp ->
            availablePackages.value?.find {mjp.packageRefId == it.packageRefId}?.apply {
                this.joPackageItemId = mjp.joPackageItemId
                this.selected = !mjp.deleted
                this.quantity = mjp.quantity
                this.deleted = mjp.deleted
            }
        }
    }

    fun putPackage(quantityModel: QuantityModel) {
        val packageItem =
            availablePackages.value?.find { it.packageRefId == quantityModel.id }?.apply {
                this.selected = true
                this.quantity = quantityModel.quantity
                this.deleted = false
            }
        _dataState.value = DataState.UpdatePackage(packageItem!!)
    }

    fun removePackage(quantityModel: QuantityModel) {
        availablePackages.value?.find { it.packageRefId == quantityModel.id }?.apply {
            this.selected = false
            this.deleted = true
            _dataState.value = DataState.UpdatePackage(this)
        }
    }

    private fun menuServiceItem(service: EntityPackageServiceWithService): MenuServiceItem {
        return MenuServiceItem(
            null,
            service.service.id,
            service.service.name!!,
            service.service.serviceRef.minutes,
            service.service.price,
            0f,
            service.service.serviceRef.serviceType,
            service.service.serviceRef.machineType,
            service.service.serviceRef.washType,
            service.serviceCrossRef.quantity,
            0
        )
    }

    private fun menuExtrasItem(extras: EntityPackageExtrasWithExtras): MenuExtrasItem {
        return MenuExtrasItem(
            null,
            extras.extras.id,
            extras.extras.name!!,
            extras.extras.price,
            0f,
            extras.extras.category,
            extras.extrasCrossRef.quantity,
        )
    }

    private fun menuProductItem(product: EntityPackageProductWithProduct): MenuProductItem {
        return MenuProductItem(
            null,
            product.product.id,
            product.product.name!!,
            product.product.price,
            0f,
            product.product.measureUnit,
            product.product.unitPerServe,
            product.productCrossRef.quantity,
            product.product.currentStock,
            product.product.productType!!
        )
    }

//    fun prepareSubmit(packageId: UUID) {
//        viewModelScope.launch {
//            repository.getById(packageId)?.let { _package ->
//                val services = mutableListOf<MenuServiceItem>()
//                val products = mutableListOf<MenuProductItem>()
//                val extras = mutableListOf<MenuExtrasItem>()
//                var discount: MenuDiscount? = null
//                var deliveryCharge: DeliveryCharge? = null
//
//                _package.services?.forEach { _packageService ->
//                    val exists = services.find { it.serviceRefId == _packageService.service.id }
//                    if (exists != null) {
//                        exists.quantity += _packageService.serviceCrossRef.quantity
//                        services.add(exists)
//                    } else {
//                        services.add(menuServiceItem(_packageService))
//                    }
//                }
//
//                _package.extras?.forEach { _packageExtras ->
//                    val exists = extras.find { it.extrasRefId == _packageExtras.extras.id }
//                    if (exists != null) {
//                        exists.quantity += _packageExtras.extrasCrossRef.quantity
//                        extras.add(exists)
//                    } else {
//                        extras.add(menuExtrasItem(_packageExtras))
//                    }
//                }
//
//                _package.products?.forEach { _packageProduct ->
//                    val exists = products.find { it.productRefId == _packageProduct.product.id }
//                    if (exists != null) {
//                        exists.quantity += _packageProduct.productCrossRef.quantity
//                        products.add(exists)
//                    } else {
//                        products.add(menuProductItem(_packageProduct))
//                    }
//                }
//
//                _dataState.value = DataState.Submit(
//                    services,
//                    products,
//                    extras,
//                    discount,
//                    deliveryCharge
//                )
//            }
//        }
//    }

    fun prepareSubmit() {
        viewModelScope.launch {
//            val list = availablePackages.value?.filter { it.selected && !it.deleted }
//            val services = mutableListOf<MenuServiceItem>()
//            val products = mutableListOf<MenuProductItem>()
//            val extras = mutableListOf<MenuExtrasItem>()
//            val discount: MenuDiscount? = null
//            val deliveryCharge: DeliveryCharge? = null

//            val ids = list?.map { it.packageRefId }
//            repository.getByIds(ids).forEach { _entityPackage ->
//                list?.find { it.packageRefId == _entityPackage.prePackage.id }
//                    ?.let { _menuPackage ->
//
//                        _entityPackage.services?.forEach { _services ->
//                            val exists =
//                                services.find { it.serviceRefId == _services.serviceCrossRef.id }
//                            if (exists != null) {
//                                exists.quantity += (_services.serviceCrossRef.quantity * _menuPackage.quantity)
//                            } else {
//                                services.add(menuServiceItem(_services).apply {
//                                    this.quantity =
//                                        _services.serviceCrossRef.quantity * _menuPackage.quantity
//                                })
//                            }
//                        }
//
//                        _entityPackage.products?.forEach { _products ->
//                            val exists =
//                                products.find { it.productRefId == _products.productCrossRef.productId }
//                            if (exists != null) {
//                                exists.quantity += (_products.productCrossRef.quantity * _menuPackage.quantity)
//                            } else {
//                                products.add(menuProductItem(_products).apply {
//                                    this.quantity =
//                                        _products.productCrossRef.quantity * _menuPackage.quantity
//                                })
//                            }
//                        }
//
//                        _entityPackage.extras?.forEach { _extras ->
//                            val exists =
//                                extras.find { it.extrasRefId == _extras.extrasCrossRef.id }
//                            if (exists != null) {
//                                exists.quantity += (_extras.extrasCrossRef.quantity * _menuPackage.quantity)
//                            } else {
//                                extras.add(menuExtrasItem(_extras).apply {
//                                    this.quantity =
//                                        _extras.extrasCrossRef.quantity * _menuPackage.quantity
//                                })
//                            }
//                        }
//                    }
//            }

//            println("before loop")
//            list?.forEach { _menuPackage ->
//                println("during loop")
//                repository.getById(_menuPackage.packageRefId)?.let { _entityPackage ->
//                    _entityPackage.services?.forEach { _services ->
//                        val exists = services.find { it.serviceRefId == _services.serviceCrossRef.serviceId }
//                        if(exists != null) {
//                            exists.quantity += (_services.serviceCrossRef.quantity * _menuPackage.quantity)
//                        } else {
//                            services.add(menuServiceItem(_services).apply {
//                                this.quantity = _services.serviceCrossRef.quantity * _menuPackage.quantity
//                            })
//                        }
//                    }
//                }
//            }
//            println("after loop")
            val packages = _availablePackages.value?.filter { it.selected || it.deleted }
            _dataState.value = DataState.Submit(
//                services,
//                products,
//                extras,
                packages,
//                discount,
//                deliveryCharge
            )

//            list?.let { menuPackages ->
//                val ids = menuPackages.map { it.packageRefId }
//                repository.getByIds(ids).let { packages ->
//                    val services = mutableListOf<MenuServiceItem>()
//                    val products = mutableListOf<MenuProductItem>()
//                    val extras = mutableListOf<MenuExtrasItem>()
//                    var discount: MenuDiscount? = null
//                    var deliveryCharge: DeliveryCharge? = null
//
//                    packages.forEach { _package ->
//                        _package.services?.forEach { _packageService ->
//                            val exists = services.find { it.serviceRefId == _packageService.service.id }
//                            if(exists != null) {
//                                exists.quantity += _packageService.serviceCrossRef.quantity
//                                services.add(exists)
//                            } else {
//                                services.add(menuServiceItem(_packageService))
//                            }
//                        }
//                    }
//
//                    _dataState.value = DataState.Submit(
//                        services,
//                        products,
//                        extras,
//                        discount,
//                        deliveryCharge
//                    )
//                }
//            }
        }
    }

    fun resetState() {
        _dataState.value = DataState.StateLess
    }

    sealed class DataState {
        object StateLess : DataState()
        data class UpdatePackage(val packageItem: MenuJobOrderPackage) : DataState()
        data class Submit(
//            val services: List<MenuServiceItem>?,
//            val products: List<MenuProductItem>?,
//            val extras: List<MenuExtrasItem>?,
            val packages: List<MenuJobOrderPackage>?,
//            val discount: MenuDiscount?,
//            val deliveryCharge: DeliveryCharge?,
        ) : DataState()
    }
}