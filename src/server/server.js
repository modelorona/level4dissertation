'use strict';

require('dotenv').config();
require('make-promises-safe');
const fastify = require('fastify')({
    logging: Boolean(process.env.LOGGING_ENABLED).valueOf()
});
const {Sequelize, DataTypes} = require('sequelize');
const opts = {
    bodyLimit: 5242880 // increase the body limit size in case the data is too big. 5mb should be enough, but will test
};

// set up the database connection
const sequelize = new Sequelize(process.env.DB_DATABASE, process.env.DB_USER, process.env.DB_PASSWORD, {
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    dialect: 'mariadb',
    dialectOptions: {
        timezone: process.env.DB_TIMEZONE
    },
    define: {
        timestamps: false
    },
    logging: () => {
    }
});

// declare the models in the database
const User = sequelize.define('User', {
    uid: {
        type: DataTypes.STRING(1024),
        allowNull: false,
        primaryKey: true,
    },
    sias: {
        type: DataTypes.INTEGER
    }
}, {
    tableName: 'user',
    timestamps: false
});

const SessionData = sequelize.define('SessionData', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
    },
    uid: {
        type: DataTypes.STRING(1024),
        allowNull: false
    },
    session_data: {
        type: DataTypes.TEXT('long')
    },
    session_start: {
        type: DataTypes.STRING(1024)
    },
    session_end: {
        type: DataTypes.STRING(1024)
    }
}, {
    tableName: 'user_session_data',
    timestamps: false
});

const AppCategory = sequelize.define('AppCategory', {
    app_name: {
        type: DataTypes.STRING(1024),
        allowNull: false,
        primaryKey: true
    },
    category: {
        type: DataTypes.STRING(1024)
    },
    app_package: {
        type: DataTypes.STRING(1024)
    }
}, {
    tableName: 'app_categories',
    timestamps: false
});

const Call = sequelize.define('Call', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
    },
    uid: {
        type: DataTypes.STRING(1024),
        allowNull: false
    },
    call_start: {
        type: DataTypes.STRING(1024)
    },
    call_end: {
        type: DataTypes.STRING(1024)
    }
}, {
    tableName: 'calls',
    timestamps: false
});

const Location = sequelize.define('Location', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: false
    },
    uid: {
        type: DataTypes.STRING(1024),
        allowNull: false
    },
    altitude: {
        type: DataTypes.DOUBLE,
        allowNull: true
    },
    hAccuracy: {
        type: DataTypes.FLOAT,
        allowNull: true
    },
    vAccuracy: {
        type: DataTypes.FLOAT,
        allowNull: true
    },
    bearing: {
        type: DataTypes.FLOAT,
        allowNull: true
    },
    bearingAccuracy: {
        type: DataTypes.FLOAT,
        allowNull: true
    },
    latitude: {
        type: DataTypes.DOUBLE,
        allowNull: true
    },
    longitude: {
        type: DataTypes.DOUBLE,
        allowNull: true
    },
    speed: {
        type: DataTypes.FLOAT,
        allowNull: true
    },
    speedAccuracy: {
        type: DataTypes.FLOAT,
        allowNull: true
    },
    timeNanos: {
        type: DataTypes.BIGINT(19),
        allowNull: true
    },
    provider: {
        type: DataTypes.STRING(1024),
        allowNull: true
    }
}, {
    tableName: 'locations',
    timestamps: false
});


sequelize.sync();

fastify.get('/', async (request, reply) => {
    let active = process.env.DB_ENABLED;
    if (active == 'true') {
        return {code: 0};
    } else {
        return {code: 1};
    }
});


fastify.post('/', opts, async (request, reply) => {
    if (!process.env.DB_ENABLED) {
        return {code: 1, reason: 'disabled'};
    }

    const reqBody = request.body;
    const appKey = reqBody.key;
    if (appKey !== process.env.APP_KEY) {
        return {code: 1, reason: 'Failed'};
    }

    const type = reqBody.type;

    try {
        if (type === 'category') {
            const exists = await AppCategory.findOne({
                where: {
                    app_name: reqBody.app_name
                }
            });
            if (!exists) {
                await AppCategory.create({
                    app_name: reqBody.app_name,
                    category: reqBody.category,
                    app_package: reqBody.app_package
                });
                console.log('category saved');
            } else {
                // if it already exists, for now do nothing
            }

        } else if (type === 'session') {
            const exists = await SessionData.findOne({
                where: {
                    uid: reqBody.uid,
                    session_start: reqBody.session_start,
                    session_end: reqBody.session_end
                }
            });
            if (!exists) {
                await SessionData.create({
                    uid: reqBody.uid,
                    session_data: reqBody.session_data,
                    session_start: reqBody.session_start,
                    session_end: reqBody.session_end
                });
                console.log('session saved');
            } else {
                //    same as above
            }

        } else if (type === 'user') {
            const exists = await User.findOne({
                where: {
                    uid: reqBody.uid
                }
            });
            if (!exists) {
                await User.create({
                    uid: reqBody.uid,
                    sias: reqBody.sias
                });
                console.log('user saved');
            } else {
                //    same as above
            }
        } else if (type === 'call') {
            const exists = await Call.findOne({
                where: {
                    uid: reqBody.uid,
                    call_start: reqBody.call_start,
                    call_end: reqBody.call_end
                }
            });
            if (!exists) {
                await Call.create({
                    uid: reqBody.uid,
                    call_start: reqBody.call_start,
                    call_end: reqBody.call_end
                });
                console.log('call saved');
            } else {
                //    same as above
            }
        } else if (type === 'location') {
            let items = reqBody.data;
            items.forEach((element) => {
                element.uid = reqBody.uid;
            });

            await Location.bulkCreate(items, {
                ignoreDuplicates: true
            }).then(() => {
                console.log('added all');
            });

        } else {
            return {code: 1, reason: 'failed'};
        }
    } catch (e) {
        console.log(e);
        fastify.log.error(e);
        return {code: 1, reason: 'dbfail'};
    }

    return {code: 0, reason: 'success'};
});

// Start listening.
fastify.listen(process.env.PORT || 3000, (err) => {
    if (err) {
        fastify.log.error(err);
        process.exit(1);
    }
});

