package org.pageflow.shared.libs;

import io.hypersistence.tsid.TSID;
import io.hypersistence.utils.common.ReflectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.EventType;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.pageflow.global.entity.TsidIdentifiable;

import java.lang.reflect.Member;
import java.util.function.Supplier;

/**
 * @author Vlad Mihalcea
 */
public class TsidGenerator implements IdentifierGenerator {

    @SuppressWarnings("NonSerializableFieldInSerializableClass")
    private final TSID.Factory tsidFactory;

    private AttributeType idType;

    public TsidGenerator(
        Tsid config,
        Member idMember,
        CustomIdGeneratorCreationContext creationContext) {
        idType = AttributeType.valueOf(ReflectionUtils.getMemberType(idMember));
        Class<? extends Supplier<TSID.Factory>> tsidSupplierClass = config.value();
        if(tsidSupplierClass.equals(Tsid.FactorySupplier.class)) {
            tsidFactory = Tsid.FactorySupplier.INSTANCE.get();
        } else {
            Supplier<TSID.Factory> factorySupplier = ReflectionUtils.newInstance(tsidSupplierClass);
            tsidFactory = factorySupplier.get();
        }
    }

    /**
     * id가 이미 할당되어있을 경우, 새로 만들지 않음.
     */
    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        if(owner instanceof TsidIdentifiable tsidIdentifiableEntity){
            TSID id = tsidIdentifiableEntity.getId();
            if(id != null) {
                return idType.cast(id);
            }
        }

        return generate(session, owner);
    }

    /**
     * @param session The session from which the request originates
     * @param object the entity or collection (idbag) for which the id is being generated
     *
     * @return
     * @throws HibernateException
     */
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object){
        return idType.cast(tsidFactory.generate());
    }

    enum AttributeType {
        LONG {
            @Override
            public Object cast(TSID tsid) {
                return tsid.toLong();
            }
        },
        STRING {
            @Override
            public Object cast(TSID tsid) {
                return tsid.toString();
            }
        },
        TSID {
            @Override
            public Object cast(TSID tsid) {
                return tsid;
            }
        };

        public abstract Object cast(TSID tsid);

        static AttributeType valueOf(Class clazz) {
            if(Long.class.isAssignableFrom(clazz)) {
                return LONG;
            } else if (String.class.isAssignableFrom(clazz)) {
                return STRING;
            } else if (TSID.class.isAssignableFrom(clazz)) {
                return TSID;
            } else {
                throw new HibernateException(
                    String.format(
                        "The @Tsid annotation on [%s] can only be placed on a Long or String entity attribute!",
                        clazz
                    )
                );
            }
        }
    }
}
