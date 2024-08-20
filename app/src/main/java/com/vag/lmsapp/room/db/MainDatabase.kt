package com.vag.lmsapp.room.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vag.lmsapp.room.dao.*
import com.vag.lmsapp.room.db.seeder.DatabaseSeeder
import com.vag.lmsapp.room.entities.*
import com.vag.lmsapp.room.migrations.*
import com.vag.lmsapp.util.converters.*

@Database(entities = [
    EntityShop::class,
    EntityUser::class,
    EntityMachine::class,
    EntityMachineRemarks::class,
    EntityMachineUsage::class,
    EntityService::class,
    EntityExtras::class,
    EntityDeliveryProfile::class,
    EntityProduct::class,
    EntityJobOrder::class,
    EntityJobOrderPackage::class,
    EntityJobOrderService::class,
    EntityJobOrderProduct::class,
    EntityJobOrderExtras::class,
    EntityJobOrderDeliveryCharge::class,
    EntityJobOrderDiscount::class,
    EntityJobOrderPayment::class,
    EntityJobOrderPictures::class,
    EntityClaimReceiptPictures::class,
    EntityPackage::class,
    EntityPackageService::class,
    EntityPackageExtras::class,
    EntityPackageProduct::class,
    EntityCustomer::class,
    EntityExpense::class,
    EntityInventoryLog::class,
    EntityDiscount::class,
    EntityActivityLog::class,
    SanctumToken::class,
    EntityTextMessageTemplate::class,
], version = 21,
    exportSchema = true,
)
@TypeConverters(
    InstantConverters::class,
    UUIDConverter::class,
    RoleConverter::class,
    WashTypeConverter::class,
    MachineTypeConverter::class,
    PaymentMethodConverter::class,
    DeliveryVehicleConverter::class,
    DeliveryOptionConverter::class,
    ProductTypeConverter::class,
    ServiceTypeConverter::class,
    DiscountApplicableConverter::class,
    ActionPermissionConverter::class,
    PaymentStatusConverter::class,
    ArrayListConverter::class,
    LocalDateConverters::class,
    JoFilterByConverter::class,
    CrudActionConverter::class
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun daoUser() : DaoUser
    abstract fun daoMachine() : DaoMachine
    abstract fun daoMachineRemarks() : DaoMachineRemarks
    abstract fun daoMachineUsage() : DaoMachineUsage
    abstract fun daoWashService() : DaoService
    abstract fun daoDeliveryProfile() : DaoDeliveryProfile
    abstract fun daoExtras() : DaoExtras
    abstract fun daoProduct() : DaoProduct
    abstract fun daoJobOrder() : DaoJobOrder
    abstract fun daoJobOrderPackage(): DaoPackage
    abstract fun daoJobOrderPayment() : DaoJobOrderPayment
    abstract fun daoCustomer() : DaoCustomer
    abstract fun daoExpense() : DaoExpense
    abstract fun daoInventoryLog() : DaoInventoryLog
    abstract fun daoDiscount() : DaoDiscount
    abstract fun daoShop() : DaoShop
    abstract fun daoJobOrderQueues() : DaoJobOrderQueues
    abstract fun daoRemote() : DaoRemote
    abstract fun daoJobOrderService(): DaoJobOrderService
    abstract fun daoJobOrderProduct(): DaoJobOrderProduct
    abstract fun daoJobOrderExtras(): DaoJobOrderExtras
    abstract fun daoJobOrderPickupDelivery(): DaoJobOrderPickupDelivery
    abstract fun daoActivityLog(): DaoActivityLog
    abstract fun daoSanctum(): DaoSanctum
    abstract fun daoSync(): DaoSync
    abstract fun daoDailyReport(): DaoDailyReport
    abstract fun daoExport(): DaoExport
    abstract fun daoTextMessageTemplate(): DaoTextMessageTemplate

    companion object {
        private const val DATABASE_NAME: String = "main_db"

        private var instance: MainDatabase? = null

        fun getInstance(context: Context) : MainDatabase {
            return instance?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, MainDatabase::class.java, DATABASE_NAME)
                    .addCallback(object: Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            DatabaseSeeder(getInstance(context)).run()
                        }
                    })
                    .addMigrations(
                        AddColumnPackagePrice(),
                        AddColumnPackageItemDeletedAt(),
                        AddForeignKeysPackageId(),
                        AddColumnPatternIds(),
                        CreateTableJobOrderPicture(),
                        AddColumnUriId(),
                        AddColumnJobOrderPicturesCreatedAt(),
                        CreateTableClaimReceiptPicture(),
                        DropColumnJobOrderPicturesFilename(),
                        DropColumnJobOrderPicturesUri(),
                        DropColumnClaimReceiptPicturesUri(),
                        AddColumnJobOrderItemsVoid(),
                        AddColumnJobOrderItemDiscountedPrice(),
                        AddColumnServiceType(),
                        CreateTableTextMessageTemplate(),
                        AddColumnPackagesHidden(),
                        AddColumnExtrasHidden(),
                        AddColumnPackageSync(),
                        AddColumnMachineUserId(),
                        AddColumnMachineUsageUserId()
//                        AddColumnPaymentVoid(),
//                        CreateTablePackage(),
//                        CreateTablePackageService(),
//                        AddColumnsIsPackage()
                    )
                    .build()
            }
        }
    }
}